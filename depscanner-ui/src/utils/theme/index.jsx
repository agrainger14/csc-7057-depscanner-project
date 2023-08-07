import { createTheme } from "@mui/material";
import typography from "./typography";

const theme = createTheme({
    palette: {
        mode: "dark",
        primary: {
            main: "#2f8af5"
        },
        background: {
            default: "#000000",
            secondary: "#FFFFFF",
        },
        text: {
            secondary: "rgba(255,255,255,0.4)"
        },
    },
    typography
})

export default theme;
