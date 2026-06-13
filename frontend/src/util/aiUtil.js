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