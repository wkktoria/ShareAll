import React from "react";
import defaultPicture from "../assets/profile.svg";
import { Link } from "react-router-dom";

const ProfileCard = (props) => {
  const { displayName, username, image } = props.user;

  let imageSource = defaultPicture;

  if (image) {
    imageSource = "/images/profile/" + image;
  }

  return (
    <div className="card">
      <div className="card-header text-center">
        <img
          className="rounded-circle shadow"
          alt="Profile image"
          width="200"
          height="200"
          src={imageSource}
        />
      </div>
      <div className="card-body text-center">
        <h4>{`${displayName}@${username}`}</h4>
      </div>
    </div>
  );
};

export default ProfileCard;
