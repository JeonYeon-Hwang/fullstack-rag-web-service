import { useState, useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import "./ShowPostsPage.css";

const BACKEND_API_BASE_URL = import.meta.env.VITE_BACKEND_API_BASE_URL;

function ShowPostsPage(){

    /* 저장용 변수 */
    const [posts, setPosts] = useState([]);
    const [page, setPage] = useState(0);
    const [error, setError] = useState('');
    const [size, setSize] = useState(10);
    const [postNum, setPostNum] = useState(0);
    const [isLoading, setIsLoading] = useState(false);

    const [newsletter, setNewsletter] = useState('');
    const [canGenerateLetter, setCanGenerateLetter] = useState(false);
    const [remainTime, setRemainTime] = useState(0);
    const [isLetterLoading, setIsLetterLoading] = useState(false);
    
    const navigate = useNavigate();
    const sentinelRef = useRef(null);
    const accessToken = localStorage.getItem("accessToken");

    /* page 기반하여 글 반환하기 */
    useEffect(() => {
        const posts = async () => {

            try{
                setIsLoading(true);

                const res = await fetch(`${BACKEND_API_BASE_URL}/post?page=${page}&size=${size}`, {
                    method: "GET",
                    credentials: "include",
                    headers: {"Content-Type": "application/json"},
                });

                if(!res.ok) throw new Error("글 불러오기 실패");

                const data = await res.json();
                
                /* 연장해서 더하기 */
                setPosts((prevPosts) => [...prevPosts, ...data]);
                setIsLoading(false);

            }catch{
                setError("글을 불러오지 못했습니다.");
            }
        };

        posts();

    }, [page, size])

    /* 스크롤 감지하기 */
    useEffect(() => {
        const observer = new IntersectionObserver((entries) =>{
            /* 특정 조건에만 페이지 수 증가 */
            if(entries[0].isIntersecting && !isLoading){
                setPage((prev) => prev + 1);
            }
        });

        if(sentinelRef.current){
            observer.observe(sentinelRef.current);
        }

        return () => observer.disconnect();
    }, [isLoading]);

    /* 글 총 갯수 가져오기 */
    useEffect(() => {
        const getNum = async () => {
            try{
                setError("");

                const res = await fetch(`${BACKEND_API_BASE_URL}/post/count`,{
                    method: "GET",
                    credentials: "include",
                });

                if(!res.ok) throw new Error("글 갯수 불러오기 실패");

                const data = await res.json();
                setPostNum(data);

            }catch{
                setError("글 갯수를 가져오지 못했습니다.");
            }
        }

        getNum();
    });


    /* 뉴스레터 생성 트리거 */
    useEffect(() => {
        const getNewsletter = async () => {
            try{
                setError("");
                /* 생성 가능한지 여부 확인하기 */
                const res_1 = await fetch(`${BACKEND_API_BASE_URL}/newsletter/perm`, {
                    method: "GET",
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${accessToken}`
                    },
                });

                const remainingMinutes = res_1.data;

                if (remainingMinutes > 0) {
                    setCanGenerateLetter(false);
                    setRemainTime(remainingMinutes);

                    /* 기존에 생성된 뉴스레터를 가져온다 */
                    const res_2 = await fetch(`${BACKEND_API_BASE_URL}/newsletter`,{
                        method: "GET",
                        headers: {
                            "Content-Type": "application/json",
                            "Authorization": `Bearer ${accessToken}`
                        },
                    });
                    
                    if(!res_2.ok) throw new Error("뉴스레터 가져오기 실패");

                    const data = await res.json();
                    setNewsletter(data)

                } else {
                    setCanGenerateLetter(true);
                    setRemainTime(0);
                }

            }catch{
                setError("뉴스레터를 가져오지 못했습니다.");
            }
        }

        getNewsletter();

    }, [accessToken]);


    /* 뉴스레터 설명 바 이벤트 */
    const handleNewsletterDescriptionBar = () => {
        if(canGenerateLetter){
            if(!isLetterLoading){
                return(
                    <div>             
                        <p>새 뉴스레터를 생성이 가능합니다.</p>
                        <button
                            type="button"
                            onClick={handleGetNewsletterClick}
                        >
                            생성하기
                        </button>
                    </div>
                );
            }else{
                return `새 뉴스레터를 생성하고 있습니다...`
            }

        }else{
            return `새 뉴스레터 생성하기 까지 ${remainTime}분 남았습니다.`;
        }
    }



    /* 해당 글로 넘어가기 */
    const handlePostClick = (postId) => {
        navigate(`/post/${postId}`);
    };

    /* 글 작성으로 넘어가기 */
    const handleCreatePostClick = () => {

        if(!accessToken){
            alert("로그인이 필요합니다");
            return;
        }

        navigate("/post");
    };

    /* 뉴스레터 생성하기 */
    const handleGetNewsletterClick = async () => {
        try{
            setError("");
            setIsLetterLoading(true);

            const data = await handleGetNewsletterClick(accessToken);
            if(!data){
                setError("뉴스레터를 생성하지 못했습니다.");
            }

            setNewsletter(data);
        }catch(error){
            throw new Error("에러: " + error);
        }finally{
            setIsLetterLoading(false);
        }
    }


    /* 날짜 형식 변환 */
    const formatCreatedAt = (createdAt) => {
        const date = new Date(createdAt);

        if(Number.isNaN(date.getTime())){
            return createdAt;
        }

        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, "0");
        const day = String(date.getDate()).padStart(2, "0");
        const hour = String(date.getHours()).padStart(2, "0");
        const minute = String(date.getMinutes()).padStart(2, "0");

        return `${year}.${month}.${day} ${hour}:${minute}`;
    };

    return (
        <div className="show-posts-page">
            <aside className="newsletter-panel">
                <div className="newsletter-notice">
                    <h1>생성형 AI 기반 뉴스레터</h1>
                    <p>다음과 같은 정보를 수집합니다: 읽기, 쓰기, 생성 </p>
                    <p>해당 정보를 바탕으로 글과 요약본을 전달해드려요</p>
                    <h1>뉴스레터를 받아보세요</h1>
                    <p>유저의 활동 데이터를 바탕으로 레터를 생성합니다</p>
                {accessToken ? (
                    <p>{handleNewsletterDescriptionBar}</p>
                ) : (
                    <p>로그인을 하여 맞춤 뉴스레터를 받아보세요</p>
                )}
                </div>
                {accessToken ? (
                    <div>
                        {newsletter}
                    </div>
                ) : (
                    <div>

                    </div>
                )}
            </aside>

            <div className="posts-header">
                <h1>질문 목록</h1>
                <button type="button" onClick={handleCreatePostClick}>질문하기</button>
            </div>

            <div className="posts-count">
                {postNum}개의 질문글이 있습니다
            </div>

            {posts && posts.map((post) => (
                <div 
                    className="post-card" 
                    key={post.id}
                    onClick={() => handlePostClick(post.postId)}
                >
                    <div className="post-comment-count">
                        <span>{post.comment_num}개 답변</span>
                    </div>
                    <div className="post-main">
                        <h2>{post.title}</h2>
                        <p className="post-content">{post.content}</p>
                        <div className="post-footer">
                            <div className="post-tags">
                                {post.tags.map((tag) => (
                                    <span key={tag}>{tag}</span>
                                ))}
                            </div>
                            <div className="post-meta">
                                <span className="post-nickname">{post.nickname}</span>
                                <span className="post-created-at">{formatCreatedAt(post.createdAt)}</span>
                            </div>
                        </div>
                    </div>
                </div>
            ))}
            <div ref={sentinelRef} style={{ height: '20px' }} />
        </div>      
    );
}

export default ShowPostsPage; 
