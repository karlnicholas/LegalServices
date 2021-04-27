import {React}  from "react";
import { BrowserRouter as Router, Route, Switch } from "react-router-dom";

import Home from "./Home";
import Statutes from "./Statutes";

export default function App(props) {
  return (
    <Router>
      <Switch>
        <Route exact path="/statutes" component={Statutes} />
        <Route exact path="/:startDate?" render={({ match }) => <Home match={match}/>}/>
      </Switch>
    </Router>
  );
};
