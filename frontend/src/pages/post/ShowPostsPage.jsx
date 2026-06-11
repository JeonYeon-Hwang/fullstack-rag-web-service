import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "./ShowPostsPage.css";

const BACKEND_API_BASE_URL = import.meta.env.VITE_BACKEND_API_BASE_URL;

function ShowPostsPage(){

    /* 저장용 변수 */
    const [posts, setPosts] = useState([]);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    /* 첫 페이지 접근 시: 글 목록 불러오기 */
    useEffect(() => {
        const posts = async () => {

            try{

                const res = await fetch(`${BACKEND_API_BASE_URL}/post`, {
                    method: "GET",
                    credentials: "include",
                    headers: {"Content-Type": "application/json"},
                });

                if(!res.ok) throw new Error("글 불러오기 실패");

                const data = await res.json();
                setPosts(data);

            }catch{
                setError("글을 불러오지 못했습니다.");
            }
        };

        posts();

    }, [])

    /* 해당 글로 넘어가기 */
    const handlePostClick = (postId) => {
        navigate(`/post/${postId}`);
    };

    /* 글 작성으로 넘어가기 */
    const handleCreatePostClick = () => {
        const accessToken = localStorage.getItem("accessToken");

        if(!accessToken){
            alert("로그인이 필요합니다");
            return;
        }

        navigate("/post");
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
            <div className="posts-header">
                <h1>질문 목록</h1>
                <button type="button" onClick={handleCreatePostClick}>질문하기</button>
            </div>

            <div className="posts-count">
                {posts.length}개의 질문글이 있습니다
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
        </div>

        
    );
}

export default ShowPostsPage; 
