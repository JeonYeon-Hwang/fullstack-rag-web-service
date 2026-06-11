export async function logout() {

    const refreshToken  = localStorage.getItem("refreshToken");
    if(!refreshToken) throw new Error("Refresh Token이 없습니다.");

    try{
        /* DB 내 refresh 토큰 삭제 */
        const response = await fetch(`${import.meta.env.VITE_BACKEND_API_BASE_URL}/logout`, {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({refreshToken}),     
        });

        /* 프론트 단의 refresh 및 access 삭제 */
        localStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");

    }catch{
        throw new Error("로그아웃에 실패하였습니다.");
    }
}