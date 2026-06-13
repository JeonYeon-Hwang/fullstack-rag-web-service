import os
from fastapi import FastAPI
from langchain_postgres.vectorstores import PGVector
from langchain_openai import OpenAIEmbeddings
from pydantic import BaseModel
from dotenv import load_dotenv

load_dotenv()

DATABASE_URL = os.getenv("DATABASE_URL")

# 서버 app 생성
app = FastAPI()


# 인자값 형식
class RecommendRequest(BaseModel):
    query: str


# 검색 준비 함수
def get_retriever():
    # 검색 질문(query)를 벡터화할 임베딩 모델
    embedding_model = OpenAIEmbeddings(model="text-embedding-3-small")

    # 틀을 만드는 과정: 
    # 경로를 열고, 임베딩 모델 준비하고, 검색 기준(cosine 등)을 정함
    # 이를 db 객체로 할당
    db = PGVector(
        connection=DATABASE_URL,
        collection_name="yeonny_pgvector",
        embeddings=embedding_model
    )

    retriever = db.as_retriever(search_kwargs={"k": 3})

    return retriever


# 추천 글 반환 api
@app.post("/recommend")
def recommend_posts(request: RecommendRequest):
    # 먼저 객체 가져오기 => 검색하여 3개 가져오기
    docs = get_retriever().invoke(request.query)

    results = []

    # 형변환 시행 
    for doc in docs:
        metadata = doc.metadata

        results.append({
            "postId": metadata.get("post_id"),
            "nickname": metadata.get("nickname"),
            "title": metadata.get("title"),
            "content": metadata.get("content"),
            "tags": metadata.get("tags", []),
            "comment_num": None,
            "createdAt": metadata.get("created_at")
        }) 


    return results


# 호출부
if __name__=="__main__":
    import uvicorn

    uvicorn.run(app, host="0.0.0.0", port=8000)