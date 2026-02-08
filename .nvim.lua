vim.o.autoread = true

vim.g.rustaceanvim = {
    server = {
        settings = {
            ["rust-analyzer"] = {
                rustfmt = {
                    extraArgs = { "+nightly" },
                },
            },
        },
    },
}

local running = false
vim.api.nvim_create_autocmd("BufWritePost", {
    pattern = "*.java",
    callback = function(args)
        if running then
            return
        end
        running = true

        vim.fn.jobstart({ "./mvnw", "spotless:apply", "-DspotlessFiles=" .. args.file }, {
            stdout_buffered = true,
            stderr_buffered = true,
            on_exit = function(_, code)
                vim.schedule(function()
                    if code ~= 0 then
                        vim.notify("spotless failed (" .. code .. ")", vim.log.levels.ERROR)
                        running = false
                        return
                    end

                    vim.cmd("checktime")

                    running = false
                end)
            end,
        })
    end,
})
