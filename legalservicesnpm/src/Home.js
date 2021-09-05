import {useState, useEffect} from "react";
import httpopinions from "./httpopinions";

import OpinionView from "./OpinionView";
import OpinionsDatesDropdown from "./OpinionsDatesDropdown";
import AppNavDropdown from "./AppNavDropdown";
import LogoNav from "./LogoNav";

export default function Home(props) {
  const [opinions, setOpinions] = useState([]);
  const [startDate, setStartDate] = useState(props.match.params.startDate);
  const[dates, setDates] = useState([]);

  useEffect(() => {
    httpopinions.get('/api/opinionviews/dates').then(response => {
      setDates(response.data);
    });
  }, []);

  useEffect(()=> {
    setStartDate(props.match.params.startDate);
  },[props]);

  useEffect(()=> {
    if ( startDate !== undefined ) {
      httpopinions.get('/api/opinionviews/cases/'+startDate)
      .then(response => {
        setOpinions(response.data);
      });
    } else {
      httpopinions.get('/api/opinionviews/cases')
      .then(response => {
        setOpinions(response.data);
      });
    }
  },[startDate]);

  return (
      <div className="container">
      <nav className="navbar navbar-expand-lg navbar-light bg-light">
        {LogoNav()}
        <button className="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent"
                aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
          <span className="navbar-toggler-icon"></span>
        </button>
        <div className="collapse navbar-collapse" id="navbarSupportedContent">
          <ul className="navbar-nav me-auto mb-2 mb-lg-0">
            <OpinionsDatesDropdown dates={dates}/>
          </ul>
        {AppNavDropdown()}
        </div>
      </nav>
      {opinions.map((opinion, index) => (<OpinionView key={index} opinion={opinion}>test</OpinionView>))}
    </div>
  );
};

