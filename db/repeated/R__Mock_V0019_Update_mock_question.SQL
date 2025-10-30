UPDATE public."Question"
SET "pointsAwarded" = 0,
    description     = '<p>example</p>',
    runtime         = '0 ms',
    memory          = 'N/A',
    code            = e'def hello_world():
    print("hello world")
',
    "submissionId"  = '157894120'
WHERE id = 'c9857a8a-9d0b-4d2e-b73c-3af2425bdca6';


