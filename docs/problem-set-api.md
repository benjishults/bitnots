


### create a problem set

`POST /api/problem-set/{source}&force-new={boolean}`

#### header

Until I have time/need to do real auth.

`Authorization: <username>`

#### body
When `source` = `tptp`:
```json5
{
  "name": "<name>",  // optional generated
  "domains": [ "SYN", "TOP" ],
  "forms": [ "FOF" ],
  "excludes": [
    {
      "domain": "SYN",
      "form": "FOF",
      "number": 2,
      "version": 1,  // optional default
      "size": -1     // optional default
    }
  ]
}
```
#### returns

If new name

`201`

```json5
{
  "id": "<newly generated UUID>",
  "name": "<given or generated name>",
  "version": 0
}
```

If name exists

`201`

```json5
{
  "id": "<newly generated UUID>",
  "name": "<name>",
  "version": <n>
}
```

where `<n>` is one greater than the previously largest version for this name.
