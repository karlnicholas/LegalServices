import React, { Component } from "react";

import UserService from "../services/user.service";

export default class BoardUser extends Component {
  constructor(props) {
    super(props);
    this.state = {
      content: null
    };
  }

  componentDidMount() {
    UserService.getUserBoard().then(
      response => {
        this.setState({
          content: response.data
        });
      },
      error => {
        this.setState({
          content:
            (error.response &&
              error.response.data &&
              error.response.data.message) ||
            error.message ||
            error.toString()
        });
      }
    );
  }

  titleGrid() {
    return this.state.content.allTitles.map((statuteTitle, index) => {
      return <tr key={index}>{this.titleRow(this.state.content.userTitles.includes(statuteTitle))}<td> </td><td>{statuteTitle}</td></tr>
    })
  }

  titleRow(selected) {
    if ( selected ) {
      return <td><input type="checkbox" checked="checked"/></td>
    } else {
      return <td><input type="checkbox" /></td>
    }
  }

  render() {
    if ( !this.state.content ) {
      return <></>;
    }
    return (
      <div className="container">
        <header className="jumbotron">
          <h3>Email: {this.state.content.email}</h3>
          <h3>Locale: {this.state.content.local}</h3>
        </header>
     <div className="panel panel-default">
       <div className="panel-heading">
         <h3 className="panel-title">Selected Statutes</h3>
       </div>
       <div className="panel-body">
         <table>
           <thead><tr><th>C1</th><th>C2</th><th>C3</th></tr></thead>
           <tbody>
           {this.titleGrid()}
           </tbody>
         </table>
       </div>
     </div>
      </div>
    );
    // return (
    //   <div className="container">
    //     <header className="jumbotron">
    //       <h3>Email: {this.state.content.email}</h3>
    //       <h3>Locale: {this.state.content.local}</h3>
    //     </header>
    //     <div className="panel panel-default">
    //       <div className="panel-heading">
    //         <h3 className="panel-title">Selected Statutes</h3>
    //       </div>
    //       <div className="panel-body">
    //         <table>
    //           <thead><tr><th>C1</th><th>C2</th><th>C3</th></tr></thead>
    //           <tbody>
    //           titleGrid({this.state.content.titles})
    //           </tbody>
    //         </table>
    //       </div>
    //     </div>
    //   </div>
    // );
  }
}
