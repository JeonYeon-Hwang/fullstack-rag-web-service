import os
from langchain_postgres.vectorstores import PGVector
from langchain_openai import OpenAIEmbeddings
from dotenv import load_dotenv

load_dotenv()

DATABASE_URL = os.getenv("DATABASE_URL")

# ingestion에서 생성한 chunck 별 임베딩 DB

# 검색 질문(query)를 벡터화할 임베딩 모델
embedding_model = OpenAIEmbeddings(model="text-embedding-3-small")

# 틀을 만드는 과정: 
# 경로를 열고, 임베딩 모델 준비하고, 검색 기준(cosine 등)을 정함
# 이를 db 객체로 할당
db = PGVector(
    connection=DATABASE_URL,
    collection_name="yeonny_pgvector",
    embedding_function=embedding_model,
    schema="vector_store"
)

# 검색 질문
query = "사람들이 가장 많이 질문하는 내용은 무엇인가?"

# 검색 설정: 상위 5개만 뽑아오기
retriever = db.as_retriever(search_kwargs={"k": 3})

# 검색 수행
relevant_docs = retriever.invoke(query)