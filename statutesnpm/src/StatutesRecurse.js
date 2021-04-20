import React from "react";

import StatuteNode from "./StatuteNode";
import StatuteLeaf from "./StatuteLeaf";


export default function StatutesRecurse(props) {
  function recurse(entries, index, navFacet) {
    if (entries[index].pathPart) {
      return (<StatutesRecurse entries={entries[index].entries} index={0} navFacet={navFacet}/>);
    } else {
      return (<StatutesRecurse entries={entries} index={index+1} navFacet={navFacet}/>);
    }
  }

  if ( props.index < props.entries.length ) {
    return ( 
        <>
        <StatuteNode entry={props.entries[props.index]} navFacet={props.navFacet}/>
        <StatuteLeaf entry={props.entries[props.index]} navFacet={props.navFacet}/>
        {recurse(props.entries, props.index, props.navFacet)}
        </>
    )
  }
  return null;
}
