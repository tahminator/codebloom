local format =
	"[%tRROR] %f:%l:%c: %m, [%tRROR] %f:%l: %m, [%tARN] %f:%l:%c: %m, [%tARN] %f:%l: %m, [%tNFO] %f:%l:%c: %m, [%tNFO] %f:%l: %m"

return {
	{
		"nvimtools/none-ls.nvim",
		event = "LazyFile",
		dependencies = { "mason.nvim" },
		config = function()
			local null_ls = require("null-ls")

			-- Setup null-ls with full override (ignores LazyVim defaults)
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
						parser = require("lint.parser").from_errorformat(format, {
							source = "checkstyle",
						}),
						extra_args = { "-c", "./checkstyle.xml" },
					}),
				},
			})

			-- Register with LazyVim for formatting
			LazyVim.format.register({
				name = "none-ls.nvim",
				priority = 300, -- Higher priority than built-in formatter
				primary = true,
				format = function(buf)
					return LazyVim.lsp.format({
						bufnr = buf,
						filter = function(client)
							return client.name == "null-ls"
						end,
					})
				end,
				sources = function(buf)
					local ret = require("null-ls.sources").get_available(vim.bo[buf].filetype, "NULL_LS_FORMATTING")
						or {}
					return vim.tbl_map(function(source)
						return source.name
					end, ret)
				end,
			})
		end,
	},
	{
		"mfussenegger/nvim-jdtls",
		config = function()
			local jdtls = require("jdtls")

			vim.api.nvim_create_autocmd("FileType", {
				pattern = "java",
				callback = function()
					-- prints and pcall are there only to give quick feedback if it works.
					print("Starting JDTLS...")
					local success, result = pcall(jdtls.start_or_attach, {
						cmd = { "jdtls" },
						settings = {
							java = {
								format = {
									settings = {
										url = vim.fn.expand("./java-formatter.xml"),
									},
								},
							},
						},
					})
					if success then
						print("JDTLS started successfully")
					else
						print("Error starting JDTLS: " .. tostring(result))
					end
				end,
			})
		end,
	},
}
