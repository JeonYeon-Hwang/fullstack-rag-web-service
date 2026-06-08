import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "./CreatePostPage.css";

const BACKEND_API_BASE_URL = import.meta.env.VITE_BACKEND_API_BASE_URL;

function CreatePostPage(){

    /* 글 등록 변수 */
    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");
    const [tags, setTags] = useState("");
    const [error, setError] = useState("");

    /* 글 등록 이벤트 */
    const handleCreatePost = async (e) => {
        e.preventDefault();
        setError("");

        /* 검증 로직 */
        if(
            title.length < 10 ||
            content.length < 30 
        ){
            setError("입력값을 다시 확인해주세요.");
            return;
        }


        try{
            /* 백엔드 접근 */
            const res = await fetch(`${BACKEND_API_BASE_URL}/post`,{
                method: "POST",
                headers: {"Content-Type": "application/json"},
                credentials: "include",
                body: JSON.stringify({title, content, tags}),
            });

            if(!res.ok) throw new Error("글 등록 실패");

        }catch{
            setError("글 등록 중 오류가 발생하였습니다.");
        }
    }


    return (
        <div className="create-post-page">
            <form onSubmit={handleCreatePost}>
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
                    <p>내용(30자 이상)</p>
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
        </div>
    );
}

export default CreatePostPage; 
