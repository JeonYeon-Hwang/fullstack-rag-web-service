import { useState, useEffect } from "react";
import { fetchWithAcess } from "../../util/fetchUtil";
import "./UserPage.css";

const BACKEND_API_BASE_URL = import.meta.env.VITE_BACKEND_API_BASE_URL;

function UserPage(){

    /* 저장용 변수 */
    const [userInfo, setUserInfo] = useState(null);
    const [error, setError] = useState('');

    /* 첫 페이지 접근 시: 유저 정보 불러오기 */
    useEffect(() => {
        const userInfo = async () => {

            try{

                const res = await fetchWithAcess(`${BACKEND_API_BASE_URL}/user`, {
                    method: "GET",
                    credentials: "include",
                    headers: {"Content-Type": "application/json"},
                });

                if(!res.ok) throw new Error("유저 정보 불러오기 실패");

                const data = await res.json();
                setUserInfo(data);

            }catch{
                setError("유저 정보를 불러오지 못했습니다.");
            }
        };

        userInfo();

    }, [])

    return (
        <main className="user-page">
            <section className="user-card">
                <div className="user-header">
                    <div className="user-avatar">
                        {userInfo?.nickname?.[0] ?? "U"}
                    </div>
                    <div>
                        <h1>내 정보</h1>
                        <p>{userInfo?.nickname ?? "사용자"}님의 계정 정보입니다.</p>
                    </div>
                </div>

                {error && <p className="user-error">{error}</p>}

                <dl className="user-info-list">
                    <div>
                        <dt>아이디</dt>
                        <dd>{userInfo?.username ?? "-"}</dd>
                    </div>
                    <div>
                        <dt>닉네임</dt>
                        <dd>{userInfo?.nickname ?? "-"}</dd>
                    </div>
                    <div>
                        <dt>이메일</dt>
                        <dd>{userInfo?.email ?? "등록된 이메일 없음"}</dd>
                    </div>
                </dl>

                <div className="user-actions">
                    <button type="button" className="secondary-button">정보 수정</button>
                    <button type="button">로그아웃</button>
                </div>
            </section>
        </main>
    );
}

export default UserPage; 
