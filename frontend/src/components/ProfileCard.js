import React from "react";
import ProfileImageWithDefault from "./ProfileImageWithDefault";
import Input from "./Input";
import ButtonWithProgress from "./ButtonWithProgress";

const ProfileCard = (props) => {
  const { displayName, username, image } = props.user;
  const showEditButton = props.isEditable && !props.inEditMode;

  return (
    <div className="card">
      <div className="card-header text-center">
        <ProfileImageWithDefault
          className="rounded-circle shadow"
          alt="Profile image"
          width="200"
          height="200"
          image={image}
        />
      </div>
      <div className="card-body text-center">
        {!props.inEditMode && <h4>{`${displayName}@${username}`}</h4>}
        {props.inEditMode && (
          <div className="mb-2">
            <Input
              value={displayName}
              label={`Change display name for ${username}`}
              onChange={props.onChangeDisplayName}
            />
          </div>
        )}
        {showEditButton && (
          <button
            className="btn btn-outline-success"
            onClick={props.onClickEdit}
          >
            <i className="fa-solid fa-user-pen"></i> Edit
          </button>
        )}
        {props.inEditMode && (
          <div>
            <ButtonWithProgress
              className="btn btn-primary"
              onClick={props.onClickSave}
              text={
                <span>
                  <i className="fa-solid fa-floppy-disk"></i> Save
                </span>
              }
              pendingApiCall={props.pendingUpdateCall}
              disabled={props.pendingUpdateCall}
            ></ButtonWithProgress>
            <button
              className="btn btn-outline-secondary ms-1"
              onClick={props.onClickCancel}
              disabled={props.pendingUpdateCall}
            >
              <i className="fa-solid fa-rectangle-xmark"></i> Cancel
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default ProfileCard;