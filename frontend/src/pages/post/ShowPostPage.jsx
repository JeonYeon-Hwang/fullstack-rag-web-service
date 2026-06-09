import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useParams } from "react-router-dom";
import "./ShowPostPage.css";

const BACKEND_API_BASE_URL = import.meta.env.VITE_BACKEND_API_BASE_URL;

function ShowPostPage(){

    /* 저장용 변수 */
    const [post, setPost] = useState(null);
    const [error, setError] = useState('');
    const { postId } = useParams();

    /* 첫 페이지 접근 시: 해당 글 불러오기 */
    useEffect(() => {
        const post = async () => {

            try{

                const res = await fetch(`${BACKEND_API_BASE_URL}/post/${postId}`, {
                    method: "GET",
                    credentials: "include"
                });

                if(!res.ok) throw new Error("글 불러오기 실패");

                const data = await res.json();
                setPost(data);

            }catch{
                setError("글을 불러오지 못했습니다.");
            }
        };

        post();

    }, [postId])

    /* 날짜 형식 변환 */
    const formatCreatedAt = (createdAt) => {
        const date = new Date(createdAt);

        if(Number.isNaN(date.getTime())){
            return createdAt;
        }

        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, "0");
        const day = String(date.getDate()).padStart(2, "0");
        const hour24 = date.getHours();
        const meridiem = hour24 < 12 ? "오전" : "오후";
        const hour = hour24 % 12 || 12;

        return `${year}년 ${month}월 ${day}일 ${meridiem} ${hour}시`;
    };

    return (
        <div className="show-post-page">
            <h1>{post?.title}</h1>
            <p className="show-post-date">
                <span>질문 날짜</span>
                {formatCreatedAt(post?.createdAt)}
            </p>
            <p className="show-post-content">{post?.content}</p>
            <div className="show-post-tags">
                {post?.tags.map((tag) => (
                    <span key={tag}>{tag}</span>
                ))}
            </div>
            <p className="show-post-author">
                <span className="show-post-author-label">작성자</span>
                <span>{post?.nickname}</span>
            </p>
        </div>
    );
}

export default ShowPostPage; 
