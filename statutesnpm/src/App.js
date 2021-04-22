import {React}  from "react";
import { BrowserRouter as Router, Route } from "react-router-dom";

import Home from "./Home";
import Statutes from "./Statutes";

export default function App(props) {
  return (
    <section className="App">
      <Router>
      <Route exact path="/" render={() => <Home />}/>
      <Route exact path="/statutes" component={Statutes} />
      </Router>
    </section>
  );
};
