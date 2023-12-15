from os import environ
from datetime import datetime
from pybuilder.core import task, init
from ddadevops import *

name = 'c4k-forgejo'
MODULE = 'federated'
PROJECT_ROOT_PATH = '../..'
version = "3.0.7-SNAPSHOT"

@init
def initialize(project):
    image_tag = version
    if "dev" in image_tag:
        image_tag += datetime.now().strftime("%Y-%m-%d-%H-%M-%S")

    input = {
        "name": name,
        "module": MODULE,
        "stage": "notused",
        "project_root_path": PROJECT_ROOT_PATH,
        "build_types": ["IMAGE"],
        "mixin_types": [],
        "image_naming": "NAME_AND_MODULE",
        "image_tag": f"{image_tag}",
    }

    project.build_depends_on("ddadevops>=4.7.0")

    build = DevopsImageBuild(project, input)
    build.initialize_build_dir()


@task
def image(project):
    build = get_devops_build(project)
    build.image()

@task
def drun(project):
    build = get_devops_build(project)
    build.drun()


@task
def test(project):
    build = get_devops_build(project)
    build.test()


@task
def publish(project):
    build = get_devops_build(project)
    build.dockerhub_login()
    build.dockerhub_publish()
