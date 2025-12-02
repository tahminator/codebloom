local format =
    "[%tRROR] %f:%l:%c: %m, [%tRROR] %f:%l: %m, [%tARN] %f:%l:%c: %m, [%tARN] %f:%l: %m, [%tNFO] %f:%l:%c: %m, [%tNFO] %f:%l: %m"

return {
    {
        "nvimtools/none-ls.nvim",
        optional = true,
        event = "LazyFile",
        dependencies = { "mason.nvim" },
        opts = function()
            local null_ls = require("null-ls")

            return {
                sources = {
                    null_ls.builtins.diagnostics.checkstyle.with({
                        args = { "-f", "sarif", "$FILENAME" },
                        parser = require("lint.parser").from_errorformat(format, {
                            source = "checkstyle",
                        }),
                        extra_args = { "-c", "./checkstyle.xml" },
                    }),
                },
            }
        end,
    },
}
