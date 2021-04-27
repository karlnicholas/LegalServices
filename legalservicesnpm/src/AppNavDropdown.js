import { Link } from "react-router-dom";

export default function AppNavDropdown() {
  return (
    <ul className="navbar-nav ml-auto">
      <li className="nav-item dropdown">
        <a className="nav-link dropdown-toggle" href="/" id="navbarDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Applications</a>
        <div className="dropdown-menu" aria-labelledby="navbarDropdown">
          <Link className="dropdown-item" to={`/`}>Court Opinions</Link>
          <Link className="dropdown-item" to={`/statutes`}>Guided Search</Link>
        </div>
      </li>
    </ul>
  );
};

