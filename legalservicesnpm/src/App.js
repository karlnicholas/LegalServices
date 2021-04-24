import {React}  from "react";
import { BrowserRouter as Router, Route } from "react-router-dom";

import Home from "./Home";
import Opinions from "./Opinions";
import Statutes from "./Statutes";

export default function App(props) {
  return (
        <Router>
        <Route exact path="/" render={() => <Home />}/>
        <Route path="/statutes" component={Statutes} />
        <Route exact path="/opinions/:startDate" component={Opinions} />
        </Router>
  );
};
