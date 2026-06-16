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

    /* 유저 기반 뉴스레터 받기 */
    const handleGetNewsletterClick = async () => {
        try{
            setError("");

            const res = await fetch(`${BACKEND_API_BASE_URL}/curate`,{
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${accessToken}`
                },
            });

            if(!res.ok) throw new Error("뉴스레터 받기 실패");
            const data = await res.json();

            setNewsletter(data.newsletter);

        }catch{
            setError("뉴스레터를 받지 못했습니다.");
        }
    };


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
                    <h1>News Letter</h1>
                </div>
            </aside>

            <div className="posts-header">
                <h1>질문 목록</h1>
                {/* <button type="button" onClick={handleGetNewsletterClick}>뉴스레터</button> */}
                <button type="button" onClick={handleCreatePostClick}>질문하기</button>
                {/* <p>{newsletter}</p> */}
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
