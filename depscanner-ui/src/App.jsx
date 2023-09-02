import { Routes, Route } from 'react-router-dom'
import Home from './pages/Home';
import About from './pages/About';
import Dashboard from './pages/Dashboard';
import Project from './pages/Project';
import UserProjects from './containers/Project/UserProjects';
import AddProject from './containers/Project/AddProject';
import Dependency from './pages/Dependency';
import Advisory from './pages/Advisory';
import DashboardRedirect from './helpers/DashboardRedirect';
import NotFound from './pages/NotFound';

function App() {
  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/about" element={<About />} />
      <Route path="/dependency/:name/:system/:version" element={<Dependency />}/>
      <Route path="/project/:id" element={<Project />} />
      <Route path="/advisory/:id" element={<Advisory />} />
      <Route path="/dashboard" element={<DashboardRedirect />} />
      <Route path="/dashboard" element={<Dashboard />}>
        <Route path="projects" element={<UserProjects />} />
        <Route path="add-project" element={<AddProject />} />
      </Route>
      <Route path="/404" element={<NotFound />} />
    </Routes>
  )
}

export default App
