/* access token 만료 시, Refresh */
export async function refreshAccessToken(params) {
    /* 1. 로컬에서 refresh 토큰을 가져온다
       2. refresh 토큰을 가지고 백엔드에 접속 
       3. 성공 시, 새 토큰을 로컬에 저장한다 */
    const refreshToken  = localStorage.getItem("refreshToken");
    if(!refreshToken) throw new Error("Refresh Token이 없습니다.");

    const response = await fetch(`${import.meta.env.VITE_BACKEND_API_BASE_URL}/jwt/refresh`, {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({refreshToken}),     
    });

    if(!response.ok) throw new Error("Access Token 갱신 실패!");

    const data = await response.json();
    localStorage.setItem("accessToken", data.accessToken);
    localStorage.setItem("refreshToken", data.refreshToken);

    return data.accessToken;
}


/* access token과 함께 fetch: 아직 구체적인 틀은 없다(url 기준) */
export async function fetchWithAcess(url, options = {}){
    /* 1. access token 가져오기 
       2. header가 없을 경우, 이를 부착 
       3. 백엔드에 요청 진행 
       4. 토큰 만료(401)면, refresh 진행 */

    let accessToken = localStorage.getItem("accessToken");
    
    if(!options.headers) options.headers = {};
    options.headers["Authorization"] = `Bearer ${accessToken}`;

    let response = await fetch(url, options);

    if(response.status === 401){
        try{
            /* refresh 함수 호출 =>  fetch 재요청 */
            accessToken = await refreshAccessToken();
            options.headers['Authorization'] = `Bearer ${accessToken}`;
            response = await fetch(url, options);
        }catch(err){
            /* refresh 실패 시 로그인 재진행 */
            localStorage.removeItem("accessToken");
            localStorage.removeItem("refreshToken");
            location.href = '/login';
        }
    }

    if(!response.ok){
        throw new Error(`HTTP 오류 : ${response.status}`);
    }

    return response;
}
