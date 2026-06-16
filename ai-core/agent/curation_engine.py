import os
import requests
import numpy as np
import psycopg

from pydantic import BaseModel
from datetime import datetime
from fastapi import APIRouter
from openai import OpenAI
from dotenv import load_dotenv

load_dotenv()

BACKEND_BASE_URL = os.getenv("BACKEND_BASE_URL", "http://localhost:8080")
DATABASE_URL = os.getenv("DATABASE_URL")

router = APIRouter()
client = OpenAI()


# 객체 정의
class ActivityItem(BaseModel):
    user_id: int
    post_id: int
    activity_type: str
    created_at: datetime

class CurateRequest(BaseModel):
    user_id: int


def unwrap_list(response_json):
    if isinstance(response_json, list):
        return response_json

    if isinstance(response_json, dict) and "value" in response_json:
        return response_json["value"]

    raise ValueError(f"Unexpected response shape: {response_json}")


# post_id로 임베딩 백터 가져오기
def get_embedding_vector(post_id: int):
    query = """
        SELECT e.embedding
        FROM langchain_pg_embedding e
        JOIN langchain_pg_collection c
            ON e.collection_id = c.uuid
        WHERE c.name = %s
            AND e.cmetadata->>'post_id' = %s
        LIMIT 1
    """

    # 쿼리문 기준으로 db 접속하여 가져오기
    with psycopg.connect(DATABASE_URL) as db:
        with db.cursor() as cur:
            cur.execute(query, ("yeonny_pgvector", str(post_id)))
            embedding = cur.fetchone()

    if embedding is None:
        return None
    
    return {
        "embedding": embedding,
    }
    

# 백엔드에서 유저기반 활동기록을 가져온다
def load_user_activities(user_id):
    print("Loading activities...")
    
    response = requests.get(f"{BACKEND_BASE_URL}/activity/{user_id}")
    response.raise_for_status()
    data = unwrap_list(response.json())

    activities = [
        ActivityItem(user_id=row['userId'], post_id=row['postId'],
                        activity_type=row['activityType'], created_at=row['created_at'])
        for row in data
    ]

    return activities


# 활동기록에 기반하여 interest 백터값을 구한다
def calculate_user_interest(activities):
    print("Calculating user interest...")
    # 가중치 설정:
    weights = {
        "VIEW": 1.0,
        "COMMENT": 2.0,
        "CREATE": 4.0
    }

    weighted_vectors = []
    total_weight = 0

    for activity in activities:
        # 1. 임베딩 백터(좌표값들)을 찾아낸다
        # 2. 가중치에 맞게 백터값을 조율한다
        # 3. 동시에, 총 값도 계속 더한다: 가중치 만큼 분모가 늘어남
        emb_vec = get_embedding_vector(activity.post_id)
        w = weights.get(activity.activity_type, 1.0)
        weighted_vectors.append(emb_vec * w)
        total_weight += w

    # 가중치 평균 계산
    user_interest_vec = np.sum(weighted_vectors, axis=0) / total_weight
    return user_interest_vec


# interest 벡터와 db속 벡터 간의 인접도를 통해 글 3개를 검색한다
def similarity_search(interest_vec):
    print("Search embedding db by interest vector...")

    query = """
        SELECT 
            e.cmetadata->>'title' AS title,
            e.cmetadata->>'content' AS content,  
            e.embedding <=> %s::vector AS distance
        FROM langchain_pg_embedding e
        JOIN langchain_pg_collection c
            ON e.collection_id = c.uuid
        WHERE c.name = 'yeonny_pgvector'
        ORDER BY e.embedding <=> %s::vector
    """

    # 받아온 interest 백터를 문자화 
    vector_txt = "[" + ",".join(map(str, interest_vec)) + "]"

    # 쿼리문 기반 db 접속 & 가져오기
    with psycopg.connect(DATABASE_URL) as db:
        with db.cursor() as cur:
            cur.execute(query, (vector_txt, vector_txt, 3))
            rows = cur.fetchall() 

    return rows


# 뽑은 글 기준으로 뉴스레터 생성하기
def generate_newsletter(curated_posts):
    print("Generating newsletter...")

    post_txt = "\n\n".join([
        f"""
        제목: {post.get("title")}
        내용: {post.get("content")}
        태그: {post.get("tags")}
        """
        for post in curated_posts
    ])

    response = client.responses.create(
        model="gpt-5.5",
        instructions="""
        너는 전문 큐레이터야.
        내가 제공한 커뮤니티 게시글을 바탕으로, 
        해당 유저에게 뉴스레터를 한국어로 작성해.
        """, 
        input=f"""
        아래 게시글을 바탕으로 뉴스레터 작성할 것.

        형식:
        제목 : 여기에 기술
        요약 : 여기에 기술
        주요 글: 여기에 요약해서 기술
        마무리 문장: 여기에 기술

        게시글 목록:
        {post_txt}
        """
    )

    print(response.output_text)
    return response.output_text


# 추천 글 반환 api
@router.post("/curate")
def curated_newsletter(request: CurateRequest):
    
    activities = load_user_activities(CurateRequest.user_id)
    interest_vec = calculate_user_interest(activities)
    curated_posts = similarity_search(interest_vec)
    newsletter = generate_newsletter(curated_posts)

    return newsletter