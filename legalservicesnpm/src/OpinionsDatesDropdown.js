import React from "react";
import { Link } from "react-router-dom";

export default function OpinionsDatesDropdown(props) {
  function getDropdown() {
    if ( props.dates.length > 0 ) {
      return props.dates.map((date, index) => (
          <Link key={index} className="dropdown-item" to={`/opinions/${date[0]}`}>{date[0]} - {date[1]}</Link>
      ))
    } else {
      return ( <a key={1} className="dropdown-item" href='/opinions'>Loading ...</a>); 
    }
  }
  return (
    <li className="nav-item dropdown">
      <a className="nav-link dropdown-toggle" href="/" id="navbarDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Opinions</a>
      <div className="dropdown-menu" aria-labelledby="navbarDropdown">
        {getDropdown()}
      </div>
    </li>
  );
};

