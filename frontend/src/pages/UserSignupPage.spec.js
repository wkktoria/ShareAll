import {
  fireEvent,
  render,
  waitFor,
  waitForElementToBeRemoved,
} from "@testing-library/react";
import { UserSignupPage } from "./UserSignupPage";

describe("UserSignupPage", () => {
  describe("Layout", () => {
    it("has header of Sign Up", () => {
      const { container } = render(<UserSignupPage />);
      const header = container.querySelector("h1");
      expect(header).toHaveTextContent("Sign Up");
    });

    it("has input for display name", () => {
      const { queryByPlaceholderText } = render(<UserSignupPage />);
      const displayNameInput = queryByPlaceholderText("Your display name");
      expect(displayNameInput).toBeInTheDocument();
    });

    it("has input for username", () => {
      const { queryByPlaceholderText } = render(<UserSignupPage />);
      const usernameInput = queryByPlaceholderText("Your username");
      expect(usernameInput).toBeInTheDocument();
    });

    it("has input for password", () => {
      const { queryByPlaceholderText } = render(<UserSignupPage />);
      const passwordInput = queryByPlaceholderText("Your password");
      expect(passwordInput).toBeInTheDocument();
    });

    it("has password type for password input", () => {
      const { queryByPlaceholderText } = render(<UserSignupPage />);
      const passwordInput = queryByPlaceholderText("Your password");
      expect(passwordInput.type).toBe("password");
    });

    it("has input for password repeat", () => {
      const { queryByPlaceholderText } = render(<UserSignupPage />);
      const passwordRepeatInput = queryByPlaceholderText(
        "Repeat your password"
      );
      expect(passwordRepeatInput).toBeInTheDocument();
    });

    it("has password type for password repeat input", () => {
      const { queryByPlaceholderText } = render(<UserSignupPage />);
      const passwordRepeatInput = queryByPlaceholderText(
        "Repeat your password"
      );
      expect(passwordRepeatInput.type).toBe("password");
    });

    it("has submit button", () => {
      const { container } = render(<UserSignupPage />);
      const button = container.querySelector("button");
      expect(button).toBeInTheDocument();
    });
  });

  describe("Interactions", () => {
    const changeEvent = (content) => {
      return {
        target: {
          value: content,
        },
      };
    };

    let button,
      displayNameInput,
      usernameInput,
      passwordInput,
      passwordRepeatInput;

    const setupForSubmit = (props) => {
      const rendered = render(<UserSignupPage {...props} />);
      const { container, queryByPlaceholderText } = rendered;

      displayNameInput = queryByPlaceholderText("Your display name");
      usernameInput = queryByPlaceholderText("Your username");
      passwordInput = queryByPlaceholderText("Your password");
      passwordRepeatInput = queryByPlaceholderText("Repeat your password");

      fireEvent.change(displayNameInput, changeEvent("my-display-name"));
      fireEvent.change(usernameInput, changeEvent("my-username"));
      fireEvent.change(passwordInput, changeEvent("P4sSw0rd"));
      fireEvent.change(passwordRepeatInput, changeEvent("P4sSw0rd"));

      button = container.querySelector("button");

      return rendered;
    };

    const mockAsyncDelayed = () => {
      return jest.fn().mockImplementation(() => {
        return new Promise((resolve, _) => {
          setTimeout(() => {
            resolve({});
          }, 300);
        });
      });
    };

    it("sets the displayName value into state", () => {
      const { queryByPlaceholderText } = render(<UserSignupPage />);
      const displayNameInput = queryByPlaceholderText("Your display name");
      fireEvent.change(displayNameInput, changeEvent("my-display-name"));
      expect(displayNameInput).toHaveValue("my-display-name");
    });

    it("sets the username value into state", () => {
      const { queryByPlaceholderText } = render(<UserSignupPage />);
      const usernameInput = queryByPlaceholderText("Your username");
      fireEvent.change(usernameInput, changeEvent("my-username"));
      expect(usernameInput).toHaveValue("my-username");
    });

    it("sets the password value into state", () => {
      const { queryByPlaceholderText } = render(<UserSignupPage />);
      const passwordInput = queryByPlaceholderText("Your password");
      fireEvent.change(passwordInput, changeEvent("P4sSw0rd"));
      expect(passwordInput).toHaveValue("P4sSw0rd");
    });

    it("sets the passwordRepeat value into state", () => {
      const { queryByPlaceholderText } = render(<UserSignupPage />);
      const passwordRepeatInput = queryByPlaceholderText(
        "Repeat your password"
      );
      fireEvent.change(passwordRepeatInput, changeEvent("P4sSw0rd"));
      expect(passwordRepeatInput).toHaveValue("P4sSw0rd");
    });

    it("calls postSignup when the fields are valid and the actions are provided in props", () => {
      const actions = {
        postSignup: jest.fn().mockResolvedValueOnce({}),
      };
      setupForSubmit({ actions });
      fireEvent.click(button);
      expect(actions.postSignup).toHaveBeenCalledTimes(1);
    });

    it("does not throw exception when clicking the button when actions not provided in props", () => {
      setupForSubmit();
      expect(() => fireEvent.click(button)).not.toThrow();
    });

    it("calls post with user body when the fields are valid", () => {
      const actions = {
        postSignup: jest.fn().mockResolvedValueOnce({}),
      };
      setupForSubmit({ actions });
      fireEvent.click(button);
      const expectedUserObject = {
        username: "my-username",
        displayName: "my-display-name",
        password: "P4sSw0rd",
      };
      expect(actions.postSignup).toHaveBeenCalledWith(expectedUserObject);
    });

    it("does not allow user to click the sign up button when there is ongoing api call", () => {
      const actions = {
        postSignup: mockAsyncDelayed(),
      };
      setupForSubmit({ actions });
      fireEvent.click(button);
      fireEvent.click(button);
      expect(actions.postSignup).toHaveBeenCalledTimes(1);
    });

    it("displays spinner when there is ongoing api call", () => {
      const actions = {
        postSignup: mockAsyncDelayed(),
      };
      const { queryByText } = setupForSubmit({ actions });
      fireEvent.click(button);
      const spinner = queryByText("Loading...");
      expect(spinner).toBeInTheDocument();
    });

    it("hides spinner after api call finishes successfully", async () => {
      const actions = {
        postSignup: mockAsyncDelayed(),
      };
      const { queryByText } = setupForSubmit({ actions });
      fireEvent.click(button);

      await waitFor(() => {
        const spinner = queryByText("Loading...");
        expect(spinner).not.toBeInTheDocument();
      });
    });

    it("hides spinner after api call finishes with error", async () => {
      const actions = {
        postSignup: jest.fn().mockImplementation(() => {
          return new Promise((resolve, reject) => {
            setTimeout(() => {
              reject({
                response: { data: {} },
              });
            }, 300);
          });
        }),
      };
      const { queryByText } = setupForSubmit({ actions });
      fireEvent.click(button);

      await waitFor(() => {
        const spinner = queryByText("Loading...");
        expect(spinner).not.toBeInTheDocument();
      });
    });

    it("displays validation error for displayName when error is received for the field", async () => {
      const actions = {
        postSignup: jest.fn().mockRejectedValue({
          response: {
            data: {
              validationErrors: {
                displayName: "Cannot be null",
              },
            },
          },
        }),
      };
      const { queryByText } = setupForSubmit({ actions });
      fireEvent.click(button);

      await waitFor(() => {
        const errorMessage = queryByText("Cannot be null");
        expect(errorMessage).toBeInTheDocument();
      });
    });

    it("enables the signup button when password and repeat password have the same value", () => {
      setupForSubmit();
      expect(button).not.toBeDisabled();
    });

    it("disables the signup button when password repeat does not match the password", () => {
      setupForSubmit();
      fireEvent.change(passwordRepeatInput, changeEvent("new-pass"));
      expect(button).toBeDisabled();
    });

    it("disables the signup button when password does not match the password repeat", () => {
      setupForSubmit();
      fireEvent.change(passwordInput, changeEvent("new-pass"));
      expect(button).toBeDisabled();
    });

    it("displays error style for password repeat input when password input repeat mismatch", () => {
      const { queryByText } = setupForSubmit();
      fireEvent.change(passwordRepeatInput, changeEvent("new-pass"));
      const mismatchWarning = queryByText("Does not match the password");
      expect(mismatchWarning).toBeInTheDocument();
    });

    it("displays error style for password repeat input when password input mismatch", () => {
      const { queryByText } = setupForSubmit();
      fireEvent.change(passwordInput, changeEvent("new-pass"));
      const mismatchWarning = queryByText("Does not match the password");
      expect(mismatchWarning).toBeInTheDocument();
    });

    it("hides the validation error when user changes the content of displayName", async () => {
      const actions = {
        postSignup: jest.fn().mockRejectedValue({
          response: {
            data: {
              validationErrors: {
                displayName: "Cannot be null",
              },
            },
          },
        }),
      };
      const { queryByText } = setupForSubmit({ actions });
      fireEvent.click(button);

      await waitFor(() => {
        queryByText("Cannot be null");
      });

      fireEvent.change(displayNameInput, changeEvent("name updated"));

      const errorMessage = queryByText("Cannot be null");
      expect(errorMessage).not.toBeInTheDocument();
    });

    it("hides the validation error when user changes the content of username", async () => {
      const actions = {
        postSignup: jest.fn().mockRejectedValue({
          response: {
            data: {
              validationErrors: {
                username: "Username cannot be null",
              },
            },
          },
        }),
      };
      const { queryByText } = setupForSubmit({ actions });
      fireEvent.click(button);

      await waitFor(() => {
        queryByText("Username cannot be null");
      });

      fireEvent.change(usernameInput, changeEvent("name updated"));

      const errorMessage = queryByText("Username cannot be null");
      expect(errorMessage).not.toBeInTheDocument();
    });

    it("hides the validation error when user changes the content of password", async () => {
      const actions = {
        postSignup: jest.fn().mockRejectedValue({
          response: {
            data: {
              validationErrors: {
                displayName: "Cannot be null",
              },
            },
          },
        }),
      };
      const { queryByText } = setupForSubmit({ actions });
      fireEvent.click(button);

      await waitFor(() => {
        queryByText("Cannot be null");
      });

      fireEvent.change(passwordInput, changeEvent("password updated"));

      const errorMessage = queryByText("Cannot be null");
      expect(errorMessage).not.toBeInTheDocument();
    });

    it("redirects to homepage after successful signup", async () => {
      const actions = {
        postSignup: jest.fn().mockResolvedValue({}),
      };
      const history = {
        push: jest.fn(),
      };
      const { queryByText } = setupForSubmit({ actions, history });
      fireEvent.click(button);

      await waitForElementToBeRemoved(() => queryByText("Loading..."));
      expect(history.push).toHaveBeenCalledWith("/");
    });
  });
});

console.error = () => {};
