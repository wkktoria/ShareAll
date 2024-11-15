import React from "react";
import defaultPicture from "../assets/profile.svg";

const ProfileImageWithDefault = (props) => {
  let imageSource = defaultPicture;

  if (props.image) {
    imageSource = `/images/profile/${props.image}`;
  }

  return (
    <img
      {...props}
      alt="Profile image"
      src={props.src || imageSource}
      onError={(event) => {
        event.target.src = defaultPicture;
      }}
    />
  );
};

export default ProfileImageWithDefault;
