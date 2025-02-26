-- Set tab size to 4 spaces locally
vim.o.tabstop = 4 -- A TAB character looks like 4 spaces
vim.o.expandtab = true -- Pressing the TAB key will insert spaces instead of a TAB character
vim.o.softtabstop = 4 -- Number of spaces inserted instead of a TAB character
vim.o.shiftwidth = 4 -- Number of spaces inserted when indenting

-- Ensure jdtls uses 4 spaces for indentation as well as format with the current formatter file.
local config = {
	settings = {
		java = {
			format = {
				settings = {
					url = "./java-formatter.xml",
					tabSize = 4,
					indentationSize = 4,
					insertSpaces = true,
				},
			},
		},
	},
}

-- Apply settings only if jdtls is running
if vim.lsp.get_active_clients({ name = "jdtls" }) then
	require("lspconfig").jdtls.setup(config)
end

-- Use local checkstyle.xml
local none_ls = require("lazy.core.config").spec.plugins["nvimtools/none-ls.nvim"]

if none_ls then
	none_ls.opts = function(_, opts)
		local nls = require("null-ls")

		-- Override sources to use local checkstyle.xml
		opts.sources = vim.list_extend(opts.sources or {}, {
			nls.builtins.diagnostics.checkstyle.with({
				extra_args = { "-c", "./checkstyle.xml" }, -- Override to use local checkstyle.xml
			}),
		})
	end
end
