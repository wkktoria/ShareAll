import React from "react";
import ProfileImageWithDefault from "./ProfileImageWithDefault";
import { Link } from "react-router-dom";

const UserListItem = (props) => {
  return (
    <Link
      to={`/${props.user.username}`}
      className="list-group-item list-group-item-action"
    >
      <ProfileImageWithDefault
        className="rounded-circle"
        alt="User profile image"
        width="32"
        height="32"
        image={props.user.image}
      />
      <span className="ps-2">{`${props.user.displayName}@${props.user.username}`}</span>
    </Link>
  );
};

export default UserListItem;
