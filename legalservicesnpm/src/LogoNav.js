import logo from "./spring-logo.png";

export default function LogoNav() {
  return (
    <a className="navbar-brand" href="/">
      <img src={logo} width="95" height="50" className="d-inline-block align-center" alt="" loading="lazy"/>
    </a>
  )
}
