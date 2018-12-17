@test "not 'CANDIDATE' or 'FINAL' tag should emit 'devSnapshot' task" {
  local expected='devSnapshot'

  local gradle_args=($(./netflix.ci --no-op))

  [[ $(array-contains ${expected} ${gradle_args[@]}) == 'true' ]]

  [[ $(array-contains 'candidate' ${gradle_args[@]}) == 'false' ]]
  [[ $(array-contains 'final' ${gradle_args[@]}) == 'false' ]]
}

@test "not 'CANDIDATE' or 'FINAL' tag should emit '-Prelease.scope=patch' option" {
  local expected='-Prelease.scope=patch'

  local gradle_args=($(./netflix.ci --no-op))

  [[ $(array-contains ${expected} ${gradle_args[@]}) == 'true' ]]
}

@test "'CANDIDATE' tag should emit 'candidate' task" {
  local expected='candidate'

  export ROCKET_TAG_TYPE='CANDIDATE'

  local gradle_args=($(./netflix.ci --no-op))

  [[ $(array-contains ${expected} ${gradle_args[@]}) == 'true' ]]

  [[ $(array-contains 'devSnapshot' ${gradle_args[@]}) == 'false' ]]
  [[ $(array-contains 'final' ${gradle_args[@]}) == 'false' ]]
}

@test "'CANDIDATE' tag should emit '-Prelease.useLastTag=true' option" {
  local expected='-Prelease.useLastTag=true'

  export ROCKET_TAG_TYPE='CANDIDATE'

  local gradle_args=($(./netflix.ci --no-op))

  [[ $(array-contains ${expected} ${gradle_args[@]}) == 'true' ]]
}

@test "'FINAL' tag should emit 'final' task" {
  local expected='final'

  export ROCKET_TAG_TYPE='FINAL'

  local gradle_args=($(./netflix.ci --no-op))

  [[ $(array-contains ${expected} ${gradle_args[@]}) == 'true' ]]

  [[ $(array-contains 'devSnapshot' ${gradle_args[@]}) == 'false' ]]
  [[ $(array-contains 'candidate' ${gradle_args[@]}) == 'false' ]]
}

@test "'FINAL' tag should emit '-Prelease.useLastTag=true' option" {
  local expected='-Prelease.useLastTag=true'

  export ROCKET_TAG_TYPE='FINAL'

  local gradle_args=($(./netflix.ci --no-op))

  [[ $(array-contains ${expected} ${gradle_args[@]}) == 'true' ]]
}

function array-contains() {
  local item="$1"
  shift

  for elt in "$@"
  do
    if [ "${elt}" == "${item}" ]
    then
      echo 'true' && exit 0
    fi
  done

  echo 'false' && exit 1
}
