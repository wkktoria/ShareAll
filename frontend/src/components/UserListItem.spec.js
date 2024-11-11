import React from "react";
import { render, fireEvent, waitFor } from "@testing-library/react";
import UserListItem from "./UserListItem";
import * as apiCalls from "../api/apiCalls";

const user = {
  username: "user1",
  displayName: "display1",
  image: "profile1.png",
};

describe("UserListItem", () => {
  it("has image", () => {
    const { container } = render(<UserListItem user={user} />);
    const image = container.querySelector("img");
    expect(image).toBeInTheDocument();
  });

  it("displays default image when user does not have one", () => {
    const userWithoutImage = {
      ...user,
      image: undefined,
    };
    const { container } = render(<UserListItem user={userWithoutImage} />);
    const image = container.querySelector("img");
    expect(image.src).toContain("/profile.svg");
  });

  it("displays users image when user have one", () => {
    const userWithoutImage = {
      ...user,
      image: undefined,
    };
    const { container } = render(<UserListItem user={user} />);
    const image = container.querySelector("img");
    expect(image.src).toContain("/images/profile/" + user.image);
  });
});
