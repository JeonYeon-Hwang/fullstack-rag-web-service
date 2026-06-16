from fastapi import FastAPI
from rag.retrieval_pipeline import router as retrieval_router
from agent.curation_engine import router as curation_router

app = FastAPI()

app.include_router(retrieval_router)
app.include_router(curation_router)


if __name__ == "__main__":
    import uvicorn

    uvicorn.run(app, host="0.0.0.0", port=8000)