import React from "react";

export default function StatuteLeaf(props) {
  if ( props.entry.sectionText ) {
    return (<div className='row'><div className='col-sm-12' dangerouslySetInnerHTML={{ __html: props.entry.text}}/></div>);
  }
  return null;
 }
