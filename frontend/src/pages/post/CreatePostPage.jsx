import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { recommend } from "../../util/aiUtil";
import "./CreatePostPage.css";

const BACKEND_API_BASE_URL = import.meta.env.VITE_BACKEND_API_BASE_URL;

function CreatePostPage(){

    /* 글 등록 변수 */
    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");
    const [tags, setTags] = useState("");
    const [error, setError] = useState("");

    /* 추천 글 변수 */
    const [recommends, setRecommends] = useState([]);
    const [lastRecommendLength, setLastRecommendLength] = useState(0);
    const [isRecommendLoading, setIsRecommendLoading] = useState(false);

    /* 자원 가져오기 */
    const accessToken = localStorage.getItem("accessToken");
    
    const navigate = useNavigate();


    /* Ai 글 추천 트리거 */
    useEffect(() => {
        if (content.length < 60) {
            setLastRecommendLength(0);
            return;
        }

        const currentStep = Math.floor(content.length / 10) * 10;

        /* 기존 범위에서 변경될 때만 */
        if(currentStep >= 60 && currentStep != lastRecommendLength){
            handleRecommendPost();
            setLastRecommendLength(currentStep);      
        }

    }, [content, lastRecommendLength]);


    /* 글 등록 이벤트 */
    const handleCreatePost = async (e) => {
        e.preventDefault();
        setError("");

        /* 검증 로직 */
        if(
            title.length < 10 ||
            content.length < 60 
        ){
            setError("입력값을 다시 확인해주세요.");
            return;
        }


        try{
            /* 백엔드 접근 */
            const res = await fetch(`${BACKEND_API_BASE_URL}/post`,{
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${accessToken}`
                },
                credentials: "include",
                body: JSON.stringify({title, content, tags}),
            });

            if(!res.ok) throw new Error("글 등록 실패");

            navigate("/");

        }catch{
            setError("글 등록 중 오류가 발생하였습니다.");
        }
    }

    /* 글 추천 이벤트 */
    const handleRecommendPost = async () => {     
        try{
            setError("");
            setIsRecommendLoading(true);

            const data = await recommend(content);
            if(!data){
                setError("추천 글을 불러오지 못했습니다.");
            }

            setRecommends(data);
        }catch(error){
            throw new Error("에러: " + error);
        }finally{
            setIsRecommendLoading(false);
        }

    }

    /* ai 설명 바 이벤트 */
    const handleAiDescriptionBar = () => {
        const contentLen = content.length;

        if (contentLen < 60 ){
            return `내용 입력 감지 중입니다.. (${contentLen}/60)`;
        }

        if(isRecommendLoading){
            return `AI가 글을 읽고 생각하고 있습니다..`;
        }else{
            const upperContentLen = (Math.floor(contentLen / 10) + 1)* 10
            return `AI가 유사한 질문을 찾았습니다. 다음 추천(${contentLen}/${upperContentLen})`;
        }
    }

    /* 해당 글로 넘어가기: 승인할 때 */
    const handlePostClick = (postId) => {
        const isConfirmed = window.confirm(
            "작성 중인 내용이 사라집니다. 이동하시겠습니까?"
        );

        if(!isConfirmed) return;

        navigate(`/post/${postId}`);
    };


    return (
        <div className="create-post-page">
            <form onSubmit={handleCreatePost}>
                <div className="create-post-header">
                    <h1>질문하기</h1>
                    <p className="required-note">필수 사항<span className="required-mark">*</span></p>
                </div>

                <div>
                    <label>제목 <span className="required-mark">*</span></label>
                    <p>제목(10자 이상)</p>
                    <input
                        type="text"
                        value={title}
                        onChange={(e) => setTitle(e.target.value)}
                    />
                </div>

                <div>
                    <label>내용 <span className="required-mark">*</span></label>
                    <p>60자 이상 입력 시, AI가 유사한 질문 글을 추천해드려요</p>
                    <textarea
                        value={content}
                        onChange={(e) => setContent(e.target.value)}
                    />
                </div>

                <div>
                    <label>태그</label>
                    <p>태그(쉼표로 태그를 분리하세요)</p>
                    <input
                        type="text"
                        value={tags}
                        onChange={(e) => setTags(e.target.value)}
                    />
                </div>

                <div>
                    <button type="submit">글 등록하기</button>
                </div>
            </form>
            <div className="recommend-panel">
                <div className="ai-description-notice">
                    <h1>AI 글 추천기능입니다</h1>
                    <p >{handleAiDescriptionBar()}</p>
                </div>

                {isRecommendLoading ? (
                    <>
                    {Array.from({ length: 3 }).map((_, index) => (
                        <div className="recommend-card recommend-card-skeleton" key={index}>
                            <div className="skeleton-title" />
                            <div className="skeleton-line" />
                            <div className="skeleton-line short" />
                            <div className="skeleton-tags">
                                <span />
                                <span />
                            </div>
                        </div>
                    ))}
                    </>
                ) : (
                    <>
                    {recommends && recommends.map((post) => (
                        <div
                            className="recommend-card"
                            key={post.postId}
                            onClick={() => handlePostClick(post.postId)}
                        >
                            <div >
                                <h2>{post.title}</h2>
                                <p>{post.content}</p>
                                <div className="recommend-tags">
                                    {post.tags?.map((tag) => (
                                        <span className="recommend-tag" key={tag}>
                                            {tag}
                                        </span>
                                    ))}
                                </div>
                            </div>
                        </div>
                    ))}
                    </> 
                )}       
            </div>
        </div>
    );
}

export default CreatePostPage; 
