import {useState, useEffect} from "react";
import queryString from "query-string";
import http from "./http-common";

import OpinionView from "./OpinionView";
  
export default function Opinions(props) {
  const [opinions, setOpinions] = useState();
  const [startDate, setStartDate] = useState(props.startDateIn);

  useEffect(()=> {
    return http.get('/api/opinionviews/cases/'+startDate)
    .then(response => {
      setOpinions(response.data);
    });
  },[props]);

  return (
    <div>
    {opinions.map((opinion, index) => (<OpinionView key={index} opinion={opinion}>test</OpinionView>))}
    </div>
  );
};

