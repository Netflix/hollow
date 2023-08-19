# Getting Support

For bug reports and feature requests, please file a [GitHub issue](https://github.com/Netflix/hollow/issues).  

If you have a question that isn't covered in this documentation, please reach out for help either on Stack Overflow or [Gitter](https://gitter.im/Netflix/hollow)

## Stack Overflow

We monitor posts tagged with `hollow`.

## Gitter

We are often available for chat via [Gitter](https://gitter.im/Netflix/hollow).  We hope that you'll stick around and pay it forward by answering other users' questions when they arise.

# Contributing to Hollow

We'll gladly review and accept pull requests for Hollow.  If you want to have a design discussion for your changes, please reach out to us on Gitter. 
There is a fake dataset of reasonable size and complexity available for testing against, please see the section on [Fake dataset](testing.md#fake-dataset). 

## Backwards Compatibility

New features in Hollow should always be added in a way that is backwards compatible, except in _extremely_ rare cases when a major version is released.

If you would like to make a contribution which breaks backwards compatibility, please contact us so we can evaluate alternate ways to achieve the desired result, and/or whether to schedule the change for an upcoming major version release.

## Dependencies

The core project _hollow_ should have zero `compile` dependencies, and should only depend on one library (jUnit) as a `test` dependency.  We believe this provides long-term stability for users, reduces licensing concerns, and eliminates the possibility that _other_ project dependencies will be compiled against incompatible versions of dependent libraries.

If you would like to make a contribution which requires a third-party dependency, please contact us before proceeding so we can discuss the appropriate location for the addition.

## sun.misc.Unsafe

The core project *hollow* utilizes `sun.misc.Unsafe`. Your IDE may treat this as an error. See [Issue #5](https://github.com/Netflix/hollow/issues/5) for how to compile without errors.
