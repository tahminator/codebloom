--- https://github.com/nvimtools/none-ls.nvim/pull/290
--- Local fix until approved

--- @class HandleCheckstyleModule
--- @field handle_checkstyle_output fun(params: any): table

--- @type HandleCheckstyleModule
---@diagnostic disable-next-line: missing-fields
local M = {}

local h = require("null-ls.helpers")

-- https://github.com/nvimtools/none-ls.nvim/blob/main/lua/null-ls/builtins/diagnostics/checkstyle.lua
local function parse_checkstyle_errors(params, output)
    if params.err:match("Must specify a config XML file.") then
        table.insert(output, {
            message = "You need to specify a configuration for checkstyle."
                .. " See https://github.com/nvimtools/none-ls.nvim/blob/main/doc/BUILTINS.md#checkstyle",
            severity = vim.diagnostic.severity.ERROR,
            bufnr = params.bufnr,
        })
        return
    end

    if params.err:match("Checkstyle ends with %d+ errors.") then
        return
    end

    table.insert(output, {
        message = vim.trim(params.err),
        severity = vim.diagnostic.severity.ERROR,
        bufnr = params.bufnr,
    })
end

-- original: https://github.com/nvimtools/none-ls.nvim/blob/main/lua/null-ls/builtins/diagnostics/checkstyle.lua
--- @param params any
--- @return table
function handle_checkstyle_output(params)
    local output = {}

    if params.err then
        parse_checkstyle_errors(params, output)
    end

    local results = params.output and params.output.runs and params.output.runs[1] and params.output.runs[1].results
        or {}

    for _, result in ipairs(results) do
        for _, location in ipairs(result.locations) do
            local col = location.physicalLocation.region.startColumn

            local parsedUri = location.physicalLocation.artifactLocation.uri:gsub("^file:", "")

            table.insert(output, {
                row = location.physicalLocation.region.startLine,
                col = col,
                end_col = col and col + 1,
                code = result.ruleId,
                message = result.message.text,
                severity = h.diagnostics.severities[result.level],
                filename = parsedUri,
            })
        end
    end

    return output
end

M.handle_checkstyle_output = handle_checkstyle_output

return M
