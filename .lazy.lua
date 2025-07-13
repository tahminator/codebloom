local format =
    "[%tRROR] %f:%l:%c: %m, [%tRROR] %f:%l: %m, [%tARN] %f:%l:%c: %m, [%tARN] %f:%l: %m, [%tNFO] %f:%l:%c: %m, [%tNFO] %f:%l: %m"

local handle_checkstyle_output = require("handle-checkstyle").handle_checkstyle_output

return {
    {
        "nvimtools/none-ls.nvim",
        event = "LazyFile",
        dependencies = { "mason.nvim" },
        config = function()
            local null_ls = require("null-ls")

            null_ls.setup({
                debug = true,
                root_dir = require("null-ls.utils").root_pattern(".null-ls-root", ".neoconf.json", "Makefile", ".git"),
                sources = {
                    null_ls.builtins.formatting.fish_indent,
                    null_ls.builtins.diagnostics.fish,
                    null_ls.builtins.formatting.stylua,
                    null_ls.builtins.formatting.shfmt,
                    null_ls.builtins.diagnostics.checkstyle.with({
                        args = { "-f", "sarif", "$FILENAME" },
                        extra_args = { "-c", "./checkstyle.xml" },
                        on_output = handle_checkstyle_output,
                    }),
                },
            })
        end,
    },
    {
        "mfussenegger/nvim-jdtls",
        opts = {
            settings = {
                java = {
                    format = {
                        settings = {
                            url = "./java-formatter.xml",
                        },
                    },
                },
            },
        },
    },
}
