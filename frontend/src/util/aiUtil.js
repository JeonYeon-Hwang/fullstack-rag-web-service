export async function recommend(query) {

    try{
        /* 서버에 추천 요청 */
        const response = await fetch(`${import.meta.env.VITE_BACKEND_API_BASE_URL}/recommend`,{
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({query}),  
        });

        if(!response.ok) throw new Error("글 추천 실패");

        const data = await response.json();
        return data;

    }catch{
        throw new Error("글 추천 중 오류가 발생하였습니다.");
    }
}


/* 유저 기반 뉴스레터 생성하여 받기 */
export async function handleGetNewsletterClick(accessToken) {
    try{
        setError("");

        const res = await fetch(`${import.meta.env.VITE_BACKEND_API_BASE_URL}/curate`,{
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${accessToken}`
            },
        });

        if(!res.ok) throw new Error("뉴스레터 생성 실패");
        
        const data = await res.json();
        return data;

    }catch{
        setError("뉴스레터를 생성하지 못했습니다.");
        }
}