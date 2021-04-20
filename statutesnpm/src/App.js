import {React, useState}  from "react";
import { BrowserRouter as Router, Route } from "react-router-dom";

import Home from "./Home";
import Statutes from "./Statutes";

export default function App(props) {
  const [path] = useState('');
  return (
    <section className="App">
      <Router>
      <Route exact path="/" render={() => <Home />}/>
      <Route exact path="/statutes:path?" component={Statutes} />
      </Router>
    </section>
  );
};
