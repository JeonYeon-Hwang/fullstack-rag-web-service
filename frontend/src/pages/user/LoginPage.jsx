import { useState } from "react";
import "./LoginPage.css";

const BACKEND_API_BASE_URL = import.meta.env.VITE_BACKEND_API_BASE_URL;

function LoginPage(){

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");

    /* 로그인 실행 */
    const handleLogin = async (e) => {
        e.preventDefault();
        setError("");

        /* 검증 로직 */
        if(
            username ==="" || password === ""
        ){
            setError("아이디와 비밀번호를 입력하세요");
            return;
        }


        try{
            /* 백엔드 접근 */
            const res = await fetch(`${BACKEND_API_BASE_URL}/login`,{
                method: "POST",
                headers: {"Content-Type": "application/json"},
                credentials: "include",
                body: JSON.stringify({username, password}),
            });

            if(!res.ok) throw new Error("로그인 실패");
            
            /* 서버 응답결과 가져오기: 이후 저장 */
            const data = await res.json();
            localStorage.setItem("accessToken", data.accessToken);
            localStorage.setItem("refreshToken", data.refreshToken);

        }catch{
            setError("아이디 또는 비밀번호가 틀렸습니다.");
        }
    }

    return (
        <main className="login-page">
            <section className="login-card">
                <div className="login-header">
                    <h1>로그인</h1>
                    <p>계정 정보를 입력해 계속 진행하세요.</p>
                </div>
            
                <form className="login-form" onSubmit={handleLogin}>
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

                    {error && <p className="form-error">{error}</p>}

                    <div className="login-actions">
                        <button type="submit">계속</button>
                    </div>
                </form>
            </section>
        </main>
    );
}

export default LoginPage;
