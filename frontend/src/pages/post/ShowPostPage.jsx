import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useParams } from "react-router-dom";
import { fetchWithAcess } from "../../util/fetchUtil";
import "./ShowPostPage.css";

const BACKEND_API_BASE_URL = import.meta.env.VITE_BACKEND_API_BASE_URL;

function ShowPostPage(){

    /* 글 변수 */
    const [post, setPost] = useState(null);
    const [error, setError] = useState('');

    /* 댓글 변수 */
    const [comment, setComment] = useState("");
    const [comments, setComments] = useState([]); 

    const { postId } = useParams();
    const navigate = useNavigate();


    /* 해당 글 불러오기 */
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

    /* 해당 글의 댓글 불러오기 */
    useEffect(() => {
        loadComments();
    },[postId])


    /* 댓글 등록 이벤트 */
    const handleCreateComment = async (e) => {
        e.preventDefault();
        setError("");

        if(comment.length < 10){
            setError("댓글 길이가 충분하지 않습니다.");
            return;
        }

        try{
            const res = await fetchWithAcess(`${BACKEND_API_BASE_URL}/comment`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                credentials: "include",
                body: JSON.stringify({ postId: post?.postId, comment }),
            });

            if(!res.ok) throw new Error("댓글 등록 실패");

            await loadComments();
            setComment(""); 

        }catch{
            setError("댓글 등록 중 오류가 발생하였습니다.");
        }
    }

    /* 댓글 불러오기 이벤트 */
    const loadComments = async () => {

        const res = await fetch(`${BACKEND_API_BASE_URL}/comments/${postId}`, {
            method: "GET",
            credentials: "include"
        });   
        
        if(!res.ok) throw new Error("댓글 불러오기 실패");

        const data = await res.json();
        setComments(data);
    }

    /* 글 수정 권한 확인 이벤트 */
    const handleEditClick = async (postId) => {
        setError("");

        try{
            const res = await fetchWithAcess(`${BACKEND_API_BASE_URL}/post/perm?postId=${postId}`, {
                method: "GET",
                credentials: "include"
            });

            if(!res.ok) throw new Error("권한 확인하기 실패");

            const data = await res.json();
            if(!data.permitted){
                alert("수정 권한이 없습니다.");
                return;
            }

            /* 수정 페이지로 이동: 본문 객체를 가지고 */
            navigate("/post/update", {
                state: {
                    data: data.post
                },
            });

        }catch{
            setError("권한 확인 과정에 문제가 발생하였습니다.");
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
        const hour24 = date.getHours();
        const meridiem = hour24 < 12 ? "오전" : "오후";
        const hour = hour24 % 12 || 12;

        return `${year}년 ${month}월 ${day}일 ${meridiem} ${hour}시`;
    };

    const formatCommentCreatedAt = (createdAt) => {
        const date = new Date(createdAt);

        if(Number.isNaN(date.getTime())){
            return createdAt;
        }

        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, "0");
        const day = String(date.getDate()).padStart(2, "0");
        const hour = String(date.getHours()).padStart(2, "0");
        const minute = String(date.getMinutes()).padStart(2, "0");

        return `${year}년 ${month}월 ${day}일 ${hour}:${minute} 에 답변함`;
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
            <div className="show-post-meta-row">
                <button
                    className="show-post-edit-button"
                    type="button"
                    onClick={() => handleEditClick(post.postId)}
                >
                    수정하기
                </button>
                <p className="show-post-author">
                    <span className="show-post-author-label">작성자</span>
                    <span>{post?.nickname}</span>
                </p>
            </div>
            
            <section className="comments-section">
                <h2>댓글 {comments?.length ?? 0}개</h2>
                {comments?.map((comment) => (
                    <div className="comment-card" key={comment?.commentId}>
                        <p className="comment-content">{comment?.comment}</p>
                        <div className="comment-meta-row">
                            <button className="comment-edit-button" type="button">수정하기</button>
                            <p className="comment-date">{formatCommentCreatedAt(comment?.createdAt)}</p>
                        </div>
                        <p className="comment-author">{comment?.nickname}</p>
                    </div>
                ))}
            </section>
            <form className="comment-form" onSubmit={handleCreateComment}>
                <div>
                    <p>댓글 달기</p>
                    <textarea
                        value={comment}
                        onChange={(e) => setComment(e.target.value)}
                    />
                </div>
                <div className="comment-actions">
                    <button type="submit">댓글 등록하기</button>
                </div>
            </form>
        </div>
    );
}

export default ShowPostPage; 
