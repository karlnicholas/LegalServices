import {React}  from "react";
import { BrowserRouter, Route, Switch } from "react-router-dom";

import Home from "./Home";
import User from "./User";
import Login from "./Login";
import Statutes from "./Statutes";
import "./App.css"

export default function App(props) {
  return (
    <BrowserRouter>
      <Switch>
        <Route exact path="/statutes" component={Statutes} />
            <Route exact path="/user" component={User} />
            <Route exact path="/login" component={Login} />
            <Route exact path="/:startDate?" render={({ match }) => <Home match={match}/>}/>
      </Switch>
    </BrowserRouter>
  );
};
