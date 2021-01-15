import React from "react";
import Container from "react-bootstrap/Container";

export default class Child2 extends React.Component {
 render() {
         
         return (
             <div>
                 OpinionView :{this.props.opinionView.name}
             </div>
         );
     }
 }
