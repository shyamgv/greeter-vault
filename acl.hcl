path "sys/*" {
  policy = "deny"
}

path "secret/*" {
  policy = "write"
}

path "auth/approle/*" {
  policy = "read"
}

path "postgresql/creds/*" {
  policy = "read"
}