import { useState, useEffect } from "react";
import { useNavigate } from 'react-router-dom';
import { fetchWithAcess } from "../util/fetchUtil";
import { logout  } from "../util/authUtil";
import "./Navbar.css";

function Navbar(){

    const BACKEND_API_BASE_URL = import.meta.env.VITE_BACKEND_API_BASE_URL;

    /* 토큰이 있는지 알아보기 */
    const accessToken = localStorage.getItem("accessToken");
    let navigate = useNavigate();
    
    /* 유저 정부 */
    const [userInfo, setUserInfo] = useState(null);
    const [error, setError] = useState('');

    /* 유저 정보 불러오기 */
    useEffect(() => {
        /* accessToken이 있을 때만 */
        if (!accessToken) return;

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

    /* 로그아웃 이벤트 */
    const handleLogout = async () => {
        await logout();
        navigate("/")
    }

    /* 전체 화면으로 이동하기 */
    const handleMenuClick = () => {
        navigate("/");
    };


    return (
        <nav className="navbar">
            <div className="navbar-inner">
                <h1 className="site-logo"
                    onClick={handleMenuClick}
                >Stack Underflow</h1>
                <div className="navbar-search-wrap">
                    <span className="navbar-search-icon">⌕</span>
                    <input
                        className="navbar-search"
                        type="search"
                        placeholder="검색어를 입력하세요.."
                    />
                </div>
                {accessToken ? (
                    <div className="navbar-user">
                        <p className="navbar-nickname">{userInfo?.nickname}</p>
                        <button 
                        type="button"
                        onClick={() => handleLogout()}
                        >로그아웃</button>
                    </div>
                ):(
                    <div className="navbar-actions">
                        <button
                        onClick={() => {
                            navigate("/login");
                        }}
                        >로그인</button>
                        <button
                        onClick={() => {
                            navigate("/join");
                        }}
                        >회원가입</button>
                    </div>
                )}
            </div>
        </nav>
    );
}

export default Navbar;
