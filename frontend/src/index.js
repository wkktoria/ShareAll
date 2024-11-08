import React from "react";
import ReactDOM from "react-dom/client";
import { HashRouter } from "react-router-dom";
import App from "./containers/App";
import { Provider } from "react-redux";
import { createStore } from "redux";
import authReducer from "./redux/authReducer";
import reportWebVitals from "./reportWebVitals";

const loggedInState = {
  id: 1,
  username: "user1",
  displayName: "display1",
  image: "profile1.png",
  password: "P@4sSw0rd",
  isLoggedIn: true,
};

const store = createStore(authReducer, loggedInState);

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(
  <Provider store={store}>
    <HashRouter>
      <App />
    </HashRouter>
  </Provider>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
