import {useState, useEffect} from "react";
import http from "./http-common";
import logo from "./spring-logo.png";

import OpinionView from "./OpinionView";
import OpinionsDatesDropdown from "./OpinionsDatesDropdown";

export default function Opinions(props) {
  const [opinions, setOpinions] = useState([]);
  const [startDate, setStartDate] = useState(props.match.params.startDate);
  const[dates, setDates] = useState([]);

  useEffect(() => {
    http.get('/api/opinionviews/dates').then(response => {
      setDates(response.data);
    });
  }, []);

  useEffect(()=> {
    setStartDate(props.match.params.startDate);
  },[props]);

  useEffect(()=> {
    if ( startDate !== undefined ) {
      http.get('/api/opinionviews/cases/'+startDate)
      .then(response => {
        setOpinions(response.data);
      });
    }
  },[startDate]);

  return (
      <div className="container">
      <nav className="navbar navbar-expand-lg navbar-light bg-light">
        <a className="navbar-brand" href="/">
          <img src={logo} width="95" height="50" className="d-inline-block align-center" alt="" loading="lazy"/>
        </a>
        <div className="collapse navbar-collapse" id="navbarSupportedContent">
          <ul className="navbar-nav me-auto mb-2 mb-lg-0">
            <OpinionsDatesDropdown dates={dates}/>
          </ul>
          <ul className="navbar-nav ml-auto">
            <li className="nav-item dropdown">
              <a className="nav-link dropdown-toggle" href="/" id="navbarDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Applications</a>
              <div className="dropdown-menu" aria-labelledby="navbarDropdown">
                <a className="dropdown-item" href="/statutes">Guided Search</a>
                <a className="dropdown-item" href="/opinions">Court Opinions</a>
              </div>
            </li>
          </ul>
        </div>
      </nav>
      {opinions.map((opinion, index) => (<OpinionView key={index} opinion={opinion}>test</OpinionView>))}
    </div>
  );
};

