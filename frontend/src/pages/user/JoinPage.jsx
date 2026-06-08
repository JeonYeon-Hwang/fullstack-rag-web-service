import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "./JoinPage.css";

const BACKEND_API_BASE_URL = import.meta.env.VITE_BACKEND_API_BASE_URL;

function JoinPage(){
    
    /* 회원가입 변수 */
    const [username, setUsername] = useState("");
    const [isUsdernameValid, setIsUsernameValid] = useState(null);
    const [password, setPassword] = useState("");
    const [nickname, setNickname] = useState("");
    const [error, setError] = useState("");

    /* 사용할 객체 */
    const navigate = useNavigate();

    /* 호출 함수들 */
    /* 입력값 검증: 상시 */
    useEffect(() => {
        /* username 중복 확인 */
        const checkUsername = async () => {
            if(username.length < 4){
                setIsUsernameValid(null);
                return;
            }

            try{
                const res = await fetch(`${BACKEND_API_BASE_URL}/user/exist`, {
                    method: "POST",
                    headers: {"Content-Type": "application/json"},
                    credentials: "include",
                    body: JSON.stringify({username}),  
                });

                /* 백엔드 응답값 기준으로 판별 */
                const exists = await res.json();
                setIsUsernameValid(!exists);

            }catch{
                setIsUsernameValid(null);
            }

        }
    },[username])


    /* 회원 가입 이벤트 */
    const handleSignUp = async (e) => {
        e.preventDefault();
        setError("");

        /* 검증 로직 */
        if(
            username.length < 4 ||
            password.length < 4 ||
            nickname.trim() === ""
        ){
            setError("입력값을 다시 확인해주세요. (모든 항목은 필수이며, ID/비밀번호는 최소 4자)");
            return;
        }


        try{
            /* 백엔드 접근: 타입 명시 */
            const res = await fetch(`${BACKEND_API_BASE_URL}/user`,{
                method: "POST",
                headers: {"Content-Type": "application/json"},
                credentials: "include",
                body: JSON.stringify({username, password, nickname}),
            });

            if(!res.ok) throw new Error("회원가입 실패");
            navigate("/login");

        }catch{
            setError("회원가입 중 오류가 발생했습니다.");
        }
    }


    return (
        <main className="join-page">
            <section className="join-card">
                <div className="join-header">
                    <h1>회원 가입</h1>
                    <p>서비스 이용을 위한 계정을 생성하세요.</p>
                </div>

                <form className="join-form" onSubmit={handleSignUp}>
                    <div className="form-field">
                        <label>아이디</label>
                        <input
                            type="text"
                            placeholder="아이디 (4자 이상)"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required
                            minLength={4}
                        />
                        {username.length >= 4 && isUsdernameValid === false && (
                            <p className="field-message field-message-error">이미 사용 중인 아이디입니다.</p>
                        )}
                        {username.length >= 4 && isUsdernameValid === true && (
                            <p className="field-message field-message-success">사용 가능한 아이디입니다.</p>
                        )}
                    </div>
                
                    <div className="form-field">
                        <label>비밀번호</label>
                        <input
                            type="text"
                            placeholder="비밀번호 (4자 이상)"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                            minLength={4}
                        />
                    </div>

                    <div className="form-field">
                        <label>닉네임</label>
                        <input
                            type="text"
                            placeholder="닉네임"
                            value={nickname}
                            onChange={(e) => setNickname(e.target.value)}
                            required
                            minLength={4}
                        />
                    </div>

                    {error && <p className="form-error">{error}</p>}

                    <div className="join-actions">
                        <button type="submit">회원 가입</button>
                    </div>
                </form>
            </section>
        </main>
    );
}

export default JoinPage;
