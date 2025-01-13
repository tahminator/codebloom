import { useEffect } from "react";
import SwaggerUI from "swagger-ui-react";

export default function SwaggerUIParent() {
  useEffect(() => {
    if (window?.location.pathname === "/swagger") {
      import("./swagger.css");
    }

    return () => {};
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [window.location.pathname]);

  return (
    <div className="!bg-white !text-black">
      <SwaggerUI url={"/v3/api-docs"} />
    </div>
  );
}
