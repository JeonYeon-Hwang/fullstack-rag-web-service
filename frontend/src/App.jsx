import { BrowserRouter, Routes, Route } from "react-router-dom";

import Navbar from "./components/Navbar";
import JoinPage from "./pages/user/JoinPage";
import LoginPage from "./pages/user/LoginPage";
import UserPage from "./pages/user/UserPage";
import CreatePostPage from "./pages/post/CreatePostPage";
import EditPostPage from "./pages/post/EditPostPage";
import ShowPostsPage from "./pages/post/ShowPostsPage";
import ShowPostPage from "./pages/post/ShowPostPage";

import './App.css'

function App() {
  return (
    <>
      <BrowserRouter>
        <Navbar/>
        <Routes>
          <Route path="/join" element={<JoinPage/>}/>
          <Route path="/login" element={<LoginPage/>}/>
          <Route path="/user" element={<UserPage/>}/>
          <Route path="/post" element={<CreatePostPage/>}/>
          <Route path="/post/update" element={<EditPostPage/>}/>
          <Route path="/" element={<ShowPostsPage/>}/>
          <Route path="/post/:postId" element={<ShowPostPage />} />
        </Routes>
      </BrowserRouter>
    </>
  )
}

export default App
