import os
from langchain_community.document_loaders import TextLoader, DirectoryLoader
from langchain_text_splitters import CharacterTextSplitter
from langchain_core.documents import Document
from langchain_openai import OpenAIEmbeddings
from langchain_postgres.vectorstores import PGVector
from dotenv import load_dotenv
import requests

load_dotenv()

BACKEND_BASE_URL = os.getenv("BACKEND_BASE_URL", "http://localhost:8080")
DATABASE_URL = os.getenv("DATABASE_URL")


def load_documents():
    print("Loading documents...")
    
    documents = []
    posts = requests.get(f"{BACKEND_BASE_URL}/post").json()
    
    for post in posts:
        post_id = post["postId"]
        comments = requests.get(f"{BACKEND_BASE_URL}/comments/{post_id}").json()

        # 들어갈 데이터 생성
        comment_text = "\n".join([c["comment"] for c in comments])
        content = f"""
        title: {post["title"]}
        tags: {",".join(post["tags"])}
        content: {post["content"]}
        comments: {comment_text}
        """
        print(content)
        documents.append(Document(
            page_content=content,
            metadata={
                "post_id": post_id,
                "title": post["title"],
                "tags": post["tags"],
                "created_at": post["createdAt"],
            }
        ))

    return documents


def create_vector_store(chunks, persist_directory=""):
    print("Create embeddings and storing in pgvector...")

    embedding_model = OpenAIEmbeddings(model="text-embedding-3-small")
    
    # openai 벡터 차원수에 맞춰 각 청크를 해당 벡터값을 가지도록 db에 저장
    # 의미있는 공간에 벡터값이 생성됨(시간이 많이 걸리는 과정)
    vectorstore = PGVector.from_documents(
        documents=chunks,
        embedding=embedding_model,
        connection=DATABASE_URL,
        collection_name="yeonny_pgvector",
        schema="vector_store"
    )

    return vectorstore


def main():
    
    # 1. 파일을 로드한다
    documents = load_documents()

    # 2. 임베딩 한 다음 생성한 벡터를 DB에 저장한다
    vectorstore = create_vector_store(documents)
    print("Successfully stored in PostgreSQL!")


if __name__ == "__main__":
    main()