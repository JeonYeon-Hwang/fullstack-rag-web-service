import { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";

const BACKEND_API_BASE_URL = import.meta.env.VITE_BACKEND_API_BASE_URL;

function EditPostPage(){

    /* 글 등록 변수: 초기에서 불러오기 */
    const { state } = useLocation();
    const post = state?.data;

    const [postId, setPostId] = useState(post?.postId || "");
    const [title, setTitle] = useState(post?.title || "");
    const [content, setContent] = useState(post?.content || "");
    const [tags, setTags] = useState(post?.tags?.join(", ") || "");
    const [error, setError] = useState("");

    /* 자원 가져오기 */
    const accessToken = localStorage.getItem("accessToken");
    
    const navigate = useNavigate();

    /* 글 수정 이벤트 */
    const handleUpdatePost = async (e) => {
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
            const res = await fetch(`${BACKEND_API_BASE_URL}/post/${postId}`,{
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${accessToken}`
                },
                credentials: "include",
                body: JSON.stringify({title, content, tags}),
            });

            if(!res.ok) throw new Error("글 수정 실패");

            const data = await res.json();

            if(data) alert("글이 수정되었습니다.");

            /* 방금 작성한 글로 이동하기 */
            navigate(`/post/${postId}`);

        }catch{
            setError("글 수정 중 오류가 발생하였습니다.");
        }
    }



    return (
        <div className="create-post-page">
            <form onSubmit={handleUpdatePost}>
                <div className="create-post-header">
                    <h1>수정하기</h1>
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
                    <button type="submit">글 수정하기</button>
                </div>
            </form>
        </div>
    );
}

export default EditPostPage; 
